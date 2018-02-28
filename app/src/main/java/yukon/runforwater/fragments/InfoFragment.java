package yukon.runforwater.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import yukon.runforwater.R;

/**
 * Created by Kieran Halliday on 2017-11-01
 */
// TODO currently can only save pdf files, generalize

public class InfoFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 111;

    private StorageReference mStorageRef;

    AdapterView<?> mParent;
    int mPosition;

    private HashMap<String, String> mapOfEducationalMaterials = new HashMap<>();

    // Array adapter to handle listview, may need to customize
    ArrayAdapter<String> adapter;
    ArrayList<String> listOfEducationalMaterials;

    private ListView listView;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.info_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        listView = getView().findViewById(R.id.list_of_files);

        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://cpen391.appspot.com");
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Educational");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mapOfEducationalMaterials.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    mapOfEducationalMaterials.put(String.valueOf(child.getValue()).replace("gs://cpen391.appspot.com/Worksheets/", ""), String.valueOf(child.getValue()));
                }
                listOfEducationalMaterials = new ArrayList<>(mapOfEducationalMaterials.keySet());
                Collections.sort(listOfEducationalMaterials, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        int firstGradeLevel = 1, secondGradeLevel = 1;
                        for (int i = 2; i <= 12; i++) {
                            if (o1.contains("Grade" + String.valueOf(i))) {
                                firstGradeLevel = i;
                            }
                            if (o2.contains("Grade" + String.valueOf(i))) {
                                secondGradeLevel = i;
                            }
                        }
                        if (firstGradeLevel == 1 || secondGradeLevel == 1) {
                            return o1.compareToIgnoreCase(o2);
                        }
                        if (firstGradeLevel < secondGradeLevel) {
                            return -1;
                        } else if (firstGradeLevel > secondGradeLevel) {
                            return 1;
                        }
                        return 0;
                    }
                });


                adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        listOfEducationalMaterials);

                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                            mParent = parent;
                            mPosition = position;
                        } else {
                            Toast.makeText(getActivity(), "Please grant storage permissions and try again", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        // No explanation needed; request the permission
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                            mParent = parent;
                            mPosition = position;
                        } else {
                            Toast.makeText(getActivity(), "Please grant storage permissions and try again", Toast.LENGTH_LONG).show();
                        }

                    }
                } else {
                    // Permissions have already been granted
                    saveFile(parent, position);
                }
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    saveFile(mParent, mPosition);
                } else {

                    Toast.makeText(getActivity(), "Permissions not granted, no file saved", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void saveFile(AdapterView<?> parent, int position) {
        if (!isExternalStorageWritable()) {
            Toast.makeText(getActivity(), "External storage not writeable, please connect an SD card", Toast.LENGTH_LONG).show();
            return;
        }

        File localFile = null;
        try {
            localFile = File.createTempFile("Worksheet", "pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert localFile != null;
        final String nameOfFile = String.valueOf(parent.getItemAtPosition(position));
        final File finalLocalFile = localFile;
        Log.d("KIERAN", nameOfFile);

        mStorageRef.child("Worksheets").child(nameOfFile).getFile(finalLocalFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Toast.makeText(getActivity(), "File downloaded successfully", Toast.LENGTH_LONG).show();
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), nameOfFile + ".pdf");

                FileChannel inChannel = null;
                FileChannel outChannel = null;
                try {
                    inChannel = new FileInputStream(finalLocalFile.getAbsolutePath()).getChannel();
                    outChannel = new FileOutputStream(file.getAbsolutePath()).getChannel();

                    inChannel.transferTo(0, inChannel.size(), outChannel);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inChannel != null)
                            inChannel.close();
                        if (outChannel != null)
                            outChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getActivity(), "Document failed to save to device, are your permissions set up properly?", Toast.LENGTH_LONG).show();
            }
        });
    }

    /* Checks if external storage is available for read and write */

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    // DEPRECATED
    //
//    /* Checks if external storage is available to at least read */
//    public boolean isExternalStorageReadable() {
//        String state = Environment.getExternalStorageState();
//        return Environment.MEDIA_MOUNTED.equals(state) ||
//                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
//    }
//
//    public void getNextPPTImage(String index) {
//
//        // Points to "Selkirk_Presentation" Directory
//        StorageReference imagesRef = mStorageRef.child("Selkirk_Presentation");
//
//        // Points to "Selkirk_Presentation/[index].jpg"
//        String pptxFileName = index + ".jpg";
//
//
//        // Insert this image into mImageView
//        Glide.with(getActivity().getApplicationContext())
//                .using(new FirebaseImageLoader())
//                .load(imagesRef.child(pptxFileName))
//                .into(mImageView);
//    }
}

import json
from pprint import pprint

# Work in progress
data = json.load(open('Run-for-Water.json'))

count = 0;
totalDist = 0;

for x in range (0, len(data["Wells"])):
    try:
        pprint(int(data["Wells"][x]["depth"]));
        count+=1;
        totalDist += int(data["Wells"][x]["depth"]);
    except:
        pprint("No element depth");
        
pprint("Average Depth: " + str(float(totalDist)/float(count)));

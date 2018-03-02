import json
from pprint import pprint

data = json.load(open('Run-for-Water.json'))

count = 0;
totalDist = 0;

for x in range (0, len(data["Wells"])):
    try:
        pprint(data["Wells"][x]["depth"]);
        count+=1;
        totalDist += data["Wells"][x]["depth"];
    except:
        pprint("No element depth");

pprint("Average Depth: " + str(float(totalDist)/float(count)));

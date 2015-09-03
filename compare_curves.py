import argparse
import matplotlib.pyplot as plt
from pylab import savefig
import os
from os import path
import csv
from glob import glob
from collections import defaultdict

parser = argparse.ArgumentParser()
parser.add_argument(dest="output_file", help="path to output folder", metavar="output_file")
parser.add_argument(dest="inputs", help="paths to input folders (max supported 3)", metavar="input_paths", nargs='+')

# Process arguments
args = parser.parse_args()

inputs = args.inputs
output_file = args.output_file

try:
    os.makedirs(path.dirname(output_file))
    print("Creating output directory")
except os.error:
    pass

def get_csv_data(in_file):
    reader = csv.reader(in_file)
    results = [(float(pH),float(titr)) for pH, titr in reader]
    return results

scatter_data = defaultdict(list)

for input_path in inputs:
    for file_name in glob(input_path+'/*.csv'):
        base_name = path.basename(file_name)
        if base_name.startswith('HId'):
            continue

        if base_name.startswith("HIe"):
            base_name = base_name.replace("HIe", "HIS")
        name = base_name.rsplit('.', 1)[0]

        with open(file_name, 'rb') as in_file:
            file_data = get_csv_data(in_file)

        scatter_data[name].append((input_path, file_data))


graph_count = len(scatter_data)
print "graph count:", graph_count
count=1
names = scatter_data.keys()
names.sort()

xticks = [float(x) for x in range(2,20,2)]
yticks = [0.0, 0.5, 1.0]

styles = (('red', 'x'), ('blue', '*'), ('green', '+'))

w,h = plt.figaspect(5.0)
fig = plt.figure(figsize=(w*5,h*5))
for name in names:
    print "Building graph for", name

    sub = fig.add_subplot(graph_count, 1, count)
    sub.set_ylim(-0.05, 1.05)
    sub.set_xlim(0, 20.0)
    sub.set_ylabel(name)

    xaxis = sub.axes.get_xaxis()
    xaxis.set_ticks(xticks)
    xaxis.set_tick_params(labelbottom=False)
    xaxis.grid(b=True, which='both')

    yaxis = sub.axes.get_yaxis()
    yaxis.set_ticks(yticks)
    yaxis.grid(b=True, which='both')

    first_cross_points = []

    for file_data, style in zip(scatter_data[name], styles):
        input_path, data = file_data
        first_cross_points.append(next((x[0] for x in data if x[1] < 0.5), 20.0))
        x, y = zip(*data)
        sub.scatter(x, y, c=style[0], marker=style[1], label=input_path)

    loc=1
    if min(first_cross_points) > 10.0:
        loc=3
    sub.legend(loc=loc)

    count += 1

#fig.show()
fig.savefig(output_file, bbox_inches='tight')






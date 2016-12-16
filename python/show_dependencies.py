#!/usr/bin/python
import os
import matplotlib.pyplot as plt
import networkx as nx

class_names = ['ForkJoinPool']
jdk8_root_folder = '../jdk8/src/share/classes'

def find_in_path(path, name):
    classes_found = set()
    for root, dirs, files in os.walk(path):
        java_files = filter(lambda filename: '.java' in filename and 'package-info' not in filename, files)
        for filename in java_files:
            if has_name(root + "/" + filename, name):
                classes_found.add(filename.rsplit('/', 2)[-1].split('.', 1)[0])
    return classes_found

def has_name(filename, name):
    lines = open(filename, 'r')
    for line in lines:
        if name in line:
            return True
    return False

# First level interests
results_dict = {}
results_dict['ForkJoinPool'] = list(find_in_path(jdk8_root_folder, 'ForkJoinPool'))
results_dict['ForkJoinWorkerThread'] = list(find_in_path(jdk8_root_folder, 'ForkJoinWorkerThread'))
results_dict['CountedCompleter'] = list(find_in_path(jdk8_root_folder, 'CountedCompleter'))
results_dict['ForkJoinTask'] = list(find_in_path(jdk8_root_folder, 'ForkJoinTask'))
results_dict['RecursiveAction'] = list(find_in_path(jdk8_root_folder, 'RecursiveAction'))
results_dict['RecursiveTask'] = list(find_in_path(jdk8_root_folder, 'RecursiveTask'))

# Second level interests
results_dict['Spliterator'] = list(find_in_path(jdk8_root_folder, 'Spliterator'))
results_dict['AbstractTask'] = list(find_in_path(jdk8_root_folder, 'AbstractTask'))


# for class_name in results_dict['ForkJoinPool']:
#     results_dict[class_name] = list(find_in_path(jdk8_root_folder, class_name))

G = nx.DiGraph()
nx.from_dict_of_lists(results_dict, create_using=G)
pos = nx.spring_layout(G)
nx.draw_networkx_labels(G, pos, fontsize=14)
nx.draw_networkx_edges(G, pos, edge_color='b', alpha=0.4, arrows=True)
plt.show()

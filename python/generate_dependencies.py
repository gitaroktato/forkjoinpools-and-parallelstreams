#!/usr/bin/python
import os
import json

class_names = ['ForkJoinPool']
jdk8_root_folder = 'C:\Users\Oresztesz_Margaritis\IdeaProjects\jdk8\src\share\classes'

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
results_dict['ManagedBlocker'] = list(find_in_path(jdk8_root_folder, 'ManagedBlocker'))
# Second level interests
results_dict['Spliterator'] = list(find_in_path(jdk8_root_folder, 'Spliterator'))
results_dict['AbstractTask'] = list(find_in_path(jdk8_root_folder, 'AbstractTask'))
results_dict['ForEachOps'] = list(find_in_path(jdk8_root_folder, 'ForEachOps'))
results_dict['FindOps'] = list(find_in_path(jdk8_root_folder, 'FindOps'))
results_dict['MatchOps'] = list(find_in_path(jdk8_root_folder, 'MatchOps'))
results_dict['SliceOps'] = list(find_in_path(jdk8_root_folder, 'SliceOps'))
results_dict['ReduceOps'] = list(find_in_path(jdk8_root_folder, 'ReduceOps'))
results_dict['DistinctOps'] = list(find_in_path(jdk8_root_folder, 'DistinctOps'))
results_dict['SortedOps'] = list(find_in_path(jdk8_root_folder, 'SortedOps'))
# Third level interests
results_dict['DoublePipeline'] = list(find_in_path(jdk8_root_folder, 'DoublePipeline'))
results_dict['LongPipeline'] = list(find_in_path(jdk8_root_folder, 'LongPipeline'))
results_dict['ReferencePipeline'] = list(find_in_path(jdk8_root_folder, 'ReferencePipeline'))
results_dict['IntPipeline'] = list(find_in_path(jdk8_root_folder, 'IntPipeline'))
# Fourth level
results_dict['StreamSupport'] = list(find_in_path(jdk8_root_folder, 'StreamSupport'))
# results_dict['Stream'] = list(find_in_path(jdk8_root_folder, 'Stream'))


# for class_name in results_dict['ForkJoinPool']:
#     results_dict[class_name] = list(find_in_path(jdk8_root_folder, class_name))

json_format = []
for key in results_dict:
    adjancency_list = results_dict[key]
    json_format.append({'name':key, 'size':1, 'imports': [element for element in adjancency_list]})
    for element in adjancency_list:
        if element not in results_dict:
            json_format.append({'name':element, 'size':1, 'imports': []})
# write json
json.dump(json_format, open('python/force/forkjoin-imports.json','w'))
print('Wrote node-link JSON data to force/force.json')

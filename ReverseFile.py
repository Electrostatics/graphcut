'''
Created on Oct 4, 2015

@author: hoga886
'''

import sys

def main():
    in_file = sys.argv[1]
    header_num = sys.argv[2]
    out_file = sys.argv[3]
    
    in_f = open(in_file, 'r')
    out_f = open(out_file, 'w')
    header = int(header_num)
    
    lines_list = list()
    header_list = list()
    
    i =  0
    for line in in_f:

        line = str(line)
        line = line.strip()
        
        if i < header:
            header_list.append(line)
            i += 1
        else:
            lines_list.append(line)
            i += 1
        
    for h in range(len(header_list)):
        out_f.write(header_list[h]+'\n')
    
    for l in range(len(lines_list)):
        out_f.write(lines_list[len(lines_list)-l-1]+'\n')
    
    out_f.close()
    in_f.close()

if __name__ == "__main__":
    main()
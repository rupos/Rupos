#!/usr/bin/python
import os
import sys

if len(sys.argv) < 2:
    print "script.py <directory>"
    exit(1)

files = os.popen2("svn list https://svn.win.tue.nl/repos/prom/Packages/")[1].read().split("\n")
files = [f.strip() for f in files if f.strip()!=""]

destDir = sys.argv[1] 
os.mkdir(destDir)

os.popen2("svn co --ignore-externals https://svn.win.tue.nl/repos/prom/Framework/trunk/ " + destDir + "/ProM")[1].read()

print "Downloaded ProM"

i = 0
for f in files:
    os.popen2("svn co --ignore-externals https://svn.win.tue.nl/repos/prom/Packages/"+f + "/Trunk " + destDir + "/" + f)[1].read()
    i += 1
    print "Downloaded ", f, "%d/%d"%(i, len(files))



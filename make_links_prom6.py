#!/usr/bin/python
import os
import sys
import shutil


if len(sys.argv) < 2:
    print "script.py <directory>"
    exit(1)

destPath = sys.argv[1]
files = [f for f in os.listdir(destPath) if f != "ProM"]

i = 0
#libFile = os.path.abspath("/".join([os.curdir, destPath, "ProM", "lib"]))
libFile = "/".join(["..", "ProM", "lib"])
libProMFile = "/".join(["..", "ProM", "dist"])
for f in files:
    try:
        shutil.rmtree("/".join([destPath, f, "lib"]))
    except:
        pass
    try:
        os.unlink("/".join([destPath, f, "lib"]))
    except:
        pass
    os.symlink(libFile, "/".join([destPath, f, "lib"]))

    try:
        shutil.rmtree("/".join([destPath, f, "packagelib"]))
    except:
        pass
    try:
        os.unlink("/".join([destPath, f, "packagelib"]))
    except:
        pass
    os.symlink(libProMFile, "/".join([destPath, f, "packagelib"]))

    try:
        shutil.rmtree("/".join([destPath, f, "stdlib"]))
    except:
        pass
    try:
        os.unlink("/".join([destPath, f, "stdlib"]))
    except:
        pass
    os.symlink(libProMFile, "/".join([destPath, f, "stdlib"]))

    try:
        os.mkdir("/".join([destPath, f, "doc"]))
    except:
        pass
    i+=1
    print "Downloaded ", f, "%d/%d"%(i, len(files))



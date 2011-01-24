#!/usr/bin/python
import random

class Entry:
    def __init__(self, name):
        self.name = name
    def gen(self, t):
        delta1 = random.randint(1, 30)
        delta2 = random.randint(delta1, 59)
        res = """
      <AuditTrailEntry>
	<WorkflowModelElement>%s</WorkflowModelElement>
	<EventType >start</EventType>
	<Timestamp>1970-01-%.2dT12:%.2d:00.000+01:00</Timestamp>
	<Originator>Guancio</Originator>
      </AuditTrailEntry>
      <AuditTrailEntry>
	<WorkflowModelElement>%s</WorkflowModelElement>
	<EventType >complete</EventType>
	<Timestamp>1970-01-%.2dT12:%.2d:00.000+01:00</Timestamp>
	<Originator>Guancio</Originator>
      </AuditTrailEntry>
        """ % (self.name, t, delta1, self.name, t, delta2)
        return (res, t+1)

class Recursion:
    def __init__(self, e):
        self.e = e
    def gen(self, t):
        res = ""
        p = 0.5
        while random.random() < p:
            (res1, t) = self.e.gen(t)
            res += res1
        return (res, t)

class Par:
    def __init__(self, l):
        self.l = l
    def gen(self, t):
        l1 = self.l[:]
        res = ""
        t1 = t
        while len(l1) > 0:
            x = random.randint(0, len(l1)-1)
            x = l1.pop(x)
            (res1, t1) = x.gen(t)
            # Non vero parallelo
            # t = t1
            res += res1
        return (res, t1)

class Sequence:
    def __init__(self, l):
        self.l = l
    def gen(self, t):
        res = ""
        exceptions = [Entry("Draft")]#, Entry("Study")]
        #p = 0.10
        p = 0.0
        for a in self.l:
            (res1, t) = a.gen(t)
            res += res1
            if random.random() < p:
                a1 = exceptions[random.randint(0, len(exceptions) - 1)]
                (res1, t) =a1.gen(t)
                res += res1
        return (res, t)
        

def gen_sequence():
    t = 0
    acts = Sequence([
            Entry("RegisterUser"),
            # Par([
            #        Entry("Draft"), 
                    # Entry("Study")
                # ]),
            Recursion(
                 Sequence([
            #         Entry("Restart"),
#                     Par([
                                 Entry("Draft"),
#                                 Entry("Study")
                                 ]),
            #         ])
                 ), 
            Entry("Review"), 
            Entry("Published")])

    exceptions = ["Draft"]

    p = 0.00

    return acts.gen(0)[0]

def gen_log(i):
    return """
    <ProcessInstance id="%i" description="PaperOne">
      %s
    </ProcessInstance>
""" % (i, gen_sequence())


print("""<?xml version="1.0" encoding="UTF-8" ?>
<WorkflowLog xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="http://is.tm.tue.nl/research/processmining/WorkflowLog.xsd" description="Sample Guancio Log File">
  <Source program="Example One from Guancio"/>
  <Process id="Paper" description="Paper Submission">
""")

for i in range(5):
    print(gen_log(i))

print("""
  </Process>
</WorkflowLog>
""")


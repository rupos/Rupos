#!/usr/bin/python
import random
import time

class Entry:
    def __init__(self, name):
        self.name = name
    def gen(self, t):
        delta1 = random.randint(1, 30*60)
        delta2 = random.randint(delta1+1, 59*60)
        t0 = time.strftime("%Y-%m-%dT%H:%M:%S.000+01:00", time.gmtime(t+delta1))
        t1 = time.strftime("%Y-%m-%dT%H:%M:%S.000+01:00", time.gmtime(t+delta2))
        res = """
      <AuditTrailEntry>
	<WorkflowModelElement>%s</WorkflowModelElement>
	<EventType >start</EventType>
	<Timestamp>%s</Timestamp>
	<Originator>Guancio</Originator>
      </AuditTrailEntry>
      <AuditTrailEntry>
	<WorkflowModelElement>%s</WorkflowModelElement>
	<EventType >complete</EventType>
	<Timestamp>%s</Timestamp>
	<Originator>Guancio</Originator>
      </AuditTrailEntry>
        """ % (self.name, t0, self.name, t1)
        return (res, t+60*60)

class Recursion:
    def __init__(self, e):
        self.e = e
    def gen(self, t):
        res = ""
        p = 0.5
        max_rec = 2
        it = 1
        while random.random() < p:
            if max_rec is not None and it > max_rec:
                break
            (res1, t) = self.e.gen(t)
            res += res1
            it += 1
        return (res, t)

class Choice:
    def __init__(self, l):
        self.l = l
    def gen(self, t):
        res = ""
        x = random.randint(0, len(self.l)-1)
        (res1, t1) = self.l[x].gen(t)
        return (res1, t1)

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
    def __init__(self, l, p=0):
        self.l = l
        self.p = p
    def gen(self, t):
        res = ""
       # exceptions = [Entry("Draft")]#, Entry("Study")]
        for a in self.l:
            (res1, t) = a.gen(t)
            res += res1
          #  if random.random() < self.p:
           #     a1 = exceptions[random.randint(0, len(exceptions) - 1)]
            #    (res1, t) =a1.gen(t)
             #   res += res1
        return (res, t)
        

def gen_sequence(t):
 #    acts = Sequence([
   #          Entry("RegisterUser"),
   #          Choice([   ])
  #           ])

    acts = Sequence([
           #  Entry("RegisterUser"),
	
             	

	
	       Recursion(
		 
               Sequence([                        
          	   Par([
          	           Entry("BookAir"),
          	           Entry("BookCar")
          	         ]),
		
			Entry("Payment")
 			]))
			
		#    ,Entry("Rejected")
		 
            #  Recursion(
                 #   Sequence([
                       # Entry("Restart"),
                  #         Par([
                  #                Entry("Draft"),  
                  #                Entry("Study")
		##		
                 #                 ])
                #           ])
              #     )
		
           #  , Recursion(
                   # Sequence([
                        #Entry("Draft")
			 #,
                        #    Par([
                     #              Entry("Study"), 
                        #            Entry("Draft")
                             #       ])
                       #   ])
                #   )
# #             Recursion(
# #                  Sequence([
# #             #         Entry("Restart"),
# # #                     Par([
# #                                  Entry("Draft"),
# # #                                 Entry("Study")
# #                                  ]),
# #             #         ])
# #                  ),
# Sequence([ 
#            Entry("Review"), 
#              # Choice([
             #           ,Entry("End")
#                     # ,Entry("Rejected")
#                     # ])
# ], p=0.1)
            ])

    exceptions = ["Draft"]

    p = 0.00

    return acts.gen(t)[0]

def gen_log(i):
    t = time.time() + 3600 * i
    return """
    <ProcessInstance id="%i" description="PaperOne">
      %s
    </ProcessInstance>
""" % (i, gen_sequence(t))


print("""<?xml version="1.0" encoding="UTF-8" ?>
<WorkflowLog xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="http://is.tm.tue.nl/research/processmining/WorkflowLog.xsd" description="Sample Guancio Log File">
  <Source program="Example One from Guancio"/>
  <Process id="Paper" description="Paper Submission">
""")

for i in range(100):
    print(gen_log(i))

print("""
  </Process>
</WorkflowLog>
""")


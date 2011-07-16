package org.processmining.plugins.petrinet.replayfitness;

public class PerformanceResult {
	int tokenCount = 0;
	float time = 0;
	float waitTime = 0;
	float synchTime = 0;
	void addToken() {
		tokenCount += 1;
	}
	void addTime(float deltaTime, float waitTime) {
		time += deltaTime;
		this.waitTime += waitTime;
		this.synchTime += (deltaTime - waitTime);
	}
	public String toString() {
		String res = "";
		res += tokenCount + " \n";
		res += time + " \n";
		res += waitTime + " \n";
		res += synchTime + " \n";
		return res;
	}
	public int getTokenCount() {
		return tokenCount;
	}
	public float getTime() {
		return time;
	}
	public float getWaitTime() {
		return waitTime;
	}
	public float getSynchTime() {
		return synchTime;
	}
	
	
}
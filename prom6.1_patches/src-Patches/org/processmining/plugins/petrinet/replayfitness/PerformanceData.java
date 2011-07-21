package org.processmining.plugins.petrinet.replayfitness;

public class PerformanceData {
	int tokenCount = 0;
	float time = 0;
	float waitTime = 0;
	float synchTime = 0;
	
	public float getSynchTime() {
		return synchTime;
	}
	public void setSynchTime(float synchTime) {
		this.synchTime = synchTime;
	}
	void addToken() {
		tokenCount += 1;
	}
	void addTime(float deltaTime, float waitTime) {
		time += deltaTime;
		this.waitTime += waitTime;
		this.synchTime += (deltaTime - waitTime);
	}
	public int getTokenCount() {
		return tokenCount;
	}
	public void setTokenCount(int tokenCount) {
		this.tokenCount = tokenCount;
	}
	public float getTime() {
		return time;
	}
	public void setTime(float time) {
		this.time = time;
	}
	public float getWaitTime() {
		return waitTime;
	}
	public void setWaitTime(float waitTime) {
		this.waitTime = waitTime;
	}
	public String toString() {
		String res = "";
		res += tokenCount + " \n";
		res += time + " \n";
		res += waitTime + " \n";
		res += synchTime + " \n";
		return res;
	}
}
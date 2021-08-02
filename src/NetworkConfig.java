// COS 445 SD5, Spring 2019
// Created by Andrew Wonnacott with Jose Rodriguez Quinones

public class NetworkConfig {
  private final int _trialCount;
  private final int _startingStake;

  public NetworkConfig(int trialCount, int startingStake) {
    _trialCount = trialCount;
    _startingStake = startingStake;
  }

  public int trialCount() {
    return _trialCount;
  }

  public int startingStake() {
    return _startingStake;
  }
}

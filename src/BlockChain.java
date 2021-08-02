// COS 445 SD5, Spring 2019
// Created by Jose Rodriguez Quinones with Andrew Wonnacott

public class BlockChain {
  private static final double blockReward = 12.5;

  private double bribe;
  private double[] spends;
  private BlockChain prev;
  private int rank;
  private double[] stake;
  private int minerIndex;
  private int roundCreated;

  // MARK: -- Initalizers

  private BlockChain(double[] stake) {
    this.bribe = 0;
    this.spends = new double[stake.length];
    this.prev = null;
    this.rank = 0;
    this.stake = stake;
    this.minerIndex = -1;
    this.roundCreated = 0;
  }

  private BlockChain(
      double bribe, double[] spends, BlockChain prev, int minerIndex, int roundCreated) {
    // Validate user inputs
    double validBribe = validBribe(bribe, prev.getBribeAmount(), prev.getStake(), minerIndex);
    double[] validSpends =
        validSpends(validBribe, prev.getBribeAmount(), spends, prev.getStake(), minerIndex);

    this.bribe = validBribe;
    this.spends = validSpends;
    this.prev = prev;
    this.rank = prev.getRank() + 1;
    this.stake = updatedStake(prev.getStake(), validSpends, minerIndex, validBribe);
    this.minerIndex = minerIndex;
    this.roundCreated = roundCreated;
  }

  // Initialize a new block chain with where stake[i]
  // represents the amount of stake miner i has to begin with.
  public static BlockChain initBlockChain(double[] stake) {
    return new BlockChain(stake);
  }

  // MARK: -- Getters

  public double getBribeAmount() {
    return bribe;
  }

  public double[] getSpends() {
    return spends;
  }

  public BlockChain getPrev() {
    return prev == null ? this : prev;
  }

  public int getRank() {
    return rank;
  }

  public double getStakeForMiner(int index) {
    return stake[index];
  }

  private double[] getStake() {
    return stake;
  }

  public int getMiner() {
    return minerIndex;
  }

  public int getRoundCreated() {
    return roundCreated;
  }

  // MARK: -- Chain Traversal
  private double[] updatedStake(double[] oldStake, double[] spends, int minerIndex, double bribe) {
    double[] newStake = new double[spends.length];

    for (int i = 0; i < spends.length; i++) {
      newStake[i] = oldStake[i] - spends[i];
    }

    // Remove the bribe from the miner's account
    newStake[minerIndex] -= bribe;

    // Credit the miner with the block reward and the bribe amount from the previous block
    newStake[minerIndex] += (prev != null) ? blockReward + prev.getBribeAmount() : blockReward;

    return newStake;
  }

  // MARK: -- Mining

  public BlockChain mine(double bribe, double[] spends, int minerIndex, int roundCreated) {
    return new BlockChain(bribe, spends, this, minerIndex, roundCreated);
  }

  // Validates the miner's bribe
  private double validBribe(double bribe, double prevBribe, double[] stake, int minerIndex) {
    double availableStake = stake[minerIndex] + prevBribe;

    if (availableStake < bribe || bribe < 0) {
      return 0;
    }

    return bribe;
  }

  // Return only the valid spends
  private double[] validSpends(
      double bribe, double prevBribe, double[] spends, double[] stake, int minerIndex) {
    double[] validSpends = new double[spends.length];

    for (int i = 0; i < spends.length; i++) {
      double availableStake = stake[i];

      if (i == minerIndex) {
        availableStake -= bribe;
      }

      if (spends[i] >= 0) {
        validSpends[i] = Math.min(spends[i], availableStake);
      }
    }

    return validSpends;
  }
}

// COS 445 SD5, Spring 2019
// Created by Jose Rodriguez Quinones with Andrew Wonnacott

public interface Miner {
  // Called once at the beginning of every round --
  // blocks is an array of valid blocks to mine on top of
  // stakeRemaining is the amount of stake the miner has remaining at the
  // given block.
  // roundsRemaining is the number of rounds until it's your turn to mine. 0 if it's this turn,
  // -1 if you never get another turn.
  public void refreshNetwork(BlockChain[] spendableBlocks, int myIndex, int roundsRemaining);

  // Choose a block to mine on top of
  public BlockChain getBlockToMine();

  // Choose an amount to spend on the next block at the next round
  public double getAmountToSpend();

  // Choose an amount to bribe for the next person to build on the block you mine
  public double getAmountToBribe();
}

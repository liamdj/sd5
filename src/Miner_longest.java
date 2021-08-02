// COS 445 SD5, Spring 2019
// Created by Jose Rodriguez Quinones with Andrew Wonnacott

import java.util.Arrays;

public class Miner_longest implements Miner {
  BlockChain[] blocks;
  int roundsLeft;
  int miningIndex;

  // Choose the longest chain, tie-breaking in favor of the earliest chain.
  private BlockChain getLongestChain() {
    BlockChain longestChain = blocks[0];
    int maxRank = 0;
    int earliest = Integer.MAX_VALUE;

    for (BlockChain block : blocks) {
      boolean shouldUpdate =
          (block.getRank() > maxRank)
              || (block.getRank() == maxRank && block.getRoundCreated() < earliest);

      if (shouldUpdate) {
        maxRank = block.getRank();
        earliest = block.getRoundCreated();
        longestChain = block;
      }
    }

    return longestChain;
  }

  public void refreshNetwork(BlockChain[] spendableBlocks, int myIndex, int roundsRemaining) {
    this.blocks = Arrays.copyOf(spendableBlocks, spendableBlocks.length);
    this.roundsLeft = roundsRemaining;
    this.miningIndex = myIndex;
  }

  public BlockChain getBlockToMine() {
    return getLongestChain();
  }

  public double getAmountToSpend() {
    BlockChain longestChain = getLongestChain();

    // Spend 1/4 of my stake on the longest chain.
    return longestChain.getStakeForMiner(miningIndex) / 4.0;
  }

  public double getAmountToBribe() {
    return 0.0;
  }
}

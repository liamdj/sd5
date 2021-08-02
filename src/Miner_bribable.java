// COS 445 SD5, Spring 2019
// Created by Jose Rodriguez Quinones with Andrew Wonnacott

import java.util.Arrays;

public class Miner_bribable implements Miner {
  BlockChain[] blocks;
  int roundsLeft;
  int miningIndex;

  // Choose the chain with the largest bribe, tie-breaking in favor of the longest chain.
  private BlockChain getDesiredChain() {
    BlockChain desiredChain = blocks[0];
    int maxRank = 0;
    double maxBribe = 0;

    for (BlockChain block : blocks) {
      boolean shouldUpdate =
          (block.getBribeAmount() > maxBribe)
              || (block.getBribeAmount() == maxBribe && block.getRank() > maxRank);

      if (shouldUpdate) {
        maxBribe = block.getBribeAmount();
        maxRank = block.getRank();
        desiredChain = block;
      }
    }

    return desiredChain;
  }

  public void refreshNetwork(BlockChain[] spendableBlocks, int myIndex, int roundsRemaining) {
    this.blocks = Arrays.copyOf(spendableBlocks, spendableBlocks.length);
    this.miningIndex = myIndex;
    this.roundsLeft = roundsRemaining;
  }

  public BlockChain getBlockToMine() {
    return getDesiredChain();
  }

  public double getAmountToSpend() {
    return 0.0;
  }

  public double getAmountToBribe() {
    return 0.0;
  }
}

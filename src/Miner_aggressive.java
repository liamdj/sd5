// COS 445 SD5, Spring 2019
// Created by Jose Rodriguez Quinones with Andrew Wonnacott

import java.util.Arrays;

public class Miner_aggressive implements Miner {
  BlockChain[] blocks;
  int roundsLeft;
  int miningIndex;

  // Choose the longest chain, tie-breaking in favor of the chain where we have the most stake.
  private BlockChain getDesiredChain() {
    BlockChain desiredChain = blocks[0];
    int maxRank = 0;
    double maxStake = 0;

    for (BlockChain block : blocks) {
      boolean shouldUpdate =
          (block.getRank() > maxRank)
              || (block.getRank() == maxRank && block.getStakeForMiner(miningIndex) > maxStake);

      if (shouldUpdate) {
        maxRank = block.getRank();
        maxStake = block.getStakeForMiner(miningIndex);
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
    BlockChain highestBlock = getDesiredChain();
    BlockChain highestBlockAncestor = highestBlock.getPrev();

    return highestBlockAncestor;
  }

  public double getAmountToSpend() {
    BlockChain highestBlock = getDesiredChain();

    // Attempt a large double spend if mining next round, otherwise, save stake until later
    if (roundsLeft == 1) {
      return highestBlock.getStakeForMiner(miningIndex) / 2.0;
    }

    return 0.0;
  }

  public double getAmountToBribe() {
    return 1.0;
  }
}

// 
// Liam Johansson (suggested by Anthony Heim)

import java.util.Arrays;

public class Miner_stupid implements Miner {
    BlockChain[] blocks;
    int roundsLeft;
    int miningIndex;

    // Choose the chain with the largest bribe, tie-breaking in favor of the longest
    // chain.
    private BlockChain getDesiredChain() {
        return blocks[0];
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
        return 0;
    }

    public double getAmountToBribe() {
        return 50;
    }
}

// COS 445 SD5, Spring 2021
// Liam Johansson

import java.util.Arrays;

public class Miner_coalition implements Miner {
    BlockChain[] blocks;
    int roundsLeft;
    int miningIndex;

    public void refreshNetwork(BlockChain[] spendableBlocks, int myIndex, int roundsRemaining) {
        this.blocks = Arrays.copyOf(spendableBlocks, spendableBlocks.length);
        this.miningIndex = myIndex;
        this.roundsLeft = roundsRemaining;
    }

    // choose the chain where you have the most money, tiebreaking by longest
    public BlockChain getBlockToMine() {
        BlockChain desiredChain = blocks[0];
        int maxRank = 0;
        double maxStake = 0;

        for (BlockChain block : blocks) {
            double stake = block.getStakeForMiner(miningIndex);
            if (block.getMiner() == miningIndex)
                stake -= block.getPrev().getBribeAmount();
            boolean shouldUpdate = (stake > maxStake) || (stake == maxStake && block.getRank() > maxRank);

            if (shouldUpdate) {
                maxRank = block.getRank();
                maxStake = stake;
                desiredChain = block;
            }
        }

        return desiredChain;
    }

    // if the next block added should be the unique longest,
    // then spend all available BTC
    public double getAmountToSpend() {
        BlockChain expectedChain = getBlockToMine();
        for (BlockChain block : blocks) {
            if (block.getRank() > expectedChain.getRank())
                return 0;
        }

        return expectedChain.getStakeForMiner(miningIndex);
    }

    public double getAmountToBribe() {
        return 0;
    }
}

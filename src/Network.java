// Testing code for Network
// COS 445 SD5, Spring 2019
// Created by Jose Rodriguez Quinones with Andrew Wonnacott

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Network extends Tournament<Miner, NetworkConfig> {
    private static final Random rand = new Random();

    Network(List<String> minerNames) {
        super(Miner.class, minerNames);
    }

    static class BlockChainComparator implements Comparator<BlockChain> {
        public int compare(BlockChain b1, BlockChain b2) {
            if (b1.getRank() != b2.getRank()) {
                return Integer.valueOf(b1.getRank()).compareTo(Integer.valueOf(b2.getRank()));
            }

            return Integer.valueOf(b2.getRoundCreated()).compareTo(Integer.valueOf(b1.getRoundCreated()));
        }
    }

    private static BlockChain getLongestChain(ArrayList<BlockChain> allBlocks) {
        return Collections.max(allBlocks, new BlockChainComparator());
    }

    // Returns true if the chain has been confirmed and is ready to finalize the
    // transactions
    private static boolean chainIsConfirmed(BlockChain chain, ArrayList<BlockChain> allBlocks) {
        // Chain is confirmed if and only if it is the unique longest chain
        return chain == getLongestChain(allBlocks);
    }

    // Returns the subset of spendable blocks given a list of blocks
    private static BlockChain[] spendableBlocks(ArrayList<BlockChain> allBlocks) {
        BlockChain[] spendableBlocks = new BlockChain[allBlocks.size()];
        return allBlocks.toArray(spendableBlocks);
    }

    // Returns the number of turns until this miner gets to mine, 0 if this turn, -1
    // if no remaining turns
    private static int getNextTurn(int minerIndex, int thisTurn, int[] miningOrder) {
        for (int i = thisTurn; i < miningOrder.length; i++) {
            if (miningOrder[i] == minerIndex) {
                return i - thisTurn;
            }
        }

        return -1;
    }

    public double[] runTrial(List<Class<? extends Miner>> strategies, NetworkConfig config) {
        List<Miner> minersList = new ArrayList<Miner>();
        for (Class<? extends Miner> minerClass : strategies) {
            try {
                minersList.add(minerClass.getDeclaredConstructor().newInstance());
            } catch (ReflectiveOperationException roe) {
                throw new RuntimeException(roe);
            }
        }
        int trialCount = config.trialCount();
        int startingStake = config.startingStake();

        Miner[] miners = new Miner[minersList.size()];
        minersList.toArray(miners);

        // Initialize students to have same utility, 0
        // Initialize map from nodes to past confirmation status
        // Initialize mining order
        double[] utilities = new double[miners.length];
        double[] initStake = new double[miners.length];
        int[] miningOrder = new int[trialCount];
        HashMap<BlockChain, Boolean> nodeConfirmationMap = new HashMap<BlockChain, Boolean>();

        Arrays.fill(initStake, startingStake);

        for (int i = 0; i < trialCount; i++) {
            miningOrder[i] = rand.nextInt(miners.length);
        }

        // Initialize a block chain and list of block chains
        ArrayList<BlockChain> validChains = new ArrayList<BlockChain>();
        validChains.add(BlockChain.initBlockChain(initStake));
        nodeConfirmationMap.put(validChains.get(0), true);

        for (int i = 0; i < trialCount; i++) {
            // Refresh every miner's network
            for (int j = 0; j < miners.length; j++) {
                miners[j].refreshNetwork(spendableBlocks(validChains), j, getNextTurn(j, i, miningOrder));
            }

            // Compile list of transactions to be spent at this turn.
            double[] transactions = new double[miners.length];

            for (int j = 0; j < miners.length; j++) {
                transactions[j] = miners[j].getAmountToSpend();
            }

            // Select a miner randomly, mine on top of their chosen block,
            int minerIndex = miningOrder[i];
            Miner thisMiner = miners[minerIndex];

            BlockChain blockToMine = thisMiner.getBlockToMine();
            assert blockToMine != null : "Passed a null block to mine";
            BlockChain newBlock = blockToMine.mine(thisMiner.getAmountToBribe(), transactions, minerIndex, (i + 1));
            nodeConfirmationMap.put(newBlock, false);
            validChains.add(newBlock);

            /*
             * System.out.println(minerIndex); System.out.println(blockToMine.getRank());
             * System.out.println(Arrays.toString(blockToMine.getStake()));
             */

            // Check pending transactions and update everyone's utility if this is now
            // a longest chain
            if (chainIsConfirmed(newBlock, validChains)) {
                // Traverse up the tree confirming all unfinished transactions
                BlockChain current = newBlock;

                while (current != null && nodeConfirmationMap.get(current) == false) {
                    nodeConfirmationMap.put(current, true);

                    // update utilities with pending transactions
                    for (int j = 0; j < miners.length; j++) {
                        utilities[j] += current.getSpends()[j];
                    }

                    current = current.getPrev();
                }
            } else {
                nodeConfirmationMap.put(newBlock, false);
            }
        }

        // Update student stake at end of game
        BlockChain finalChain = getLongestChain(validChains);
        for (int j = 0; j < miners.length; j++) {
            utilities[j] += finalChain.getStakeForMiner(j);
        }

        return utilities;
    }

    public static void main(String[] args) throws java.io.FileNotFoundException {
        assert args.length >= 1 : "Expected filename of strategies as first argument";
        final int numTrials = 1;
        final BufferedReader namesFile = new BufferedReader(new FileReader(args[0]));
        final List<String> strategyNames = namesFile.lines().map(s -> String.format("Miner_%s", s))
                .collect(Collectors.toList());
        final int N = strategyNames.size();
        final Network withStrategies = new Network(strategyNames);

        double[] res = withStrategies.oneEachTrials(numTrials, new NetworkConfig(10000, 100));

        System.out.println("netID,score");
        for (int i = 0; i != N; ++i) {
            System.out.println(strategyNames.get(i).substring(6) + "," + Double.toString(res[i]));
        }

        // System.out.println("netID, average score, number");
        // String prevName = strategyNames.get(0);
        // double cumScore = res[0];
        // int stratCount = 1;
        // for (int i = 1; i < N; i++) {
        // String name = strategyNames.get(i);
        // if (!prevName.equals(name)) {
        // System.out.println(prevName + ", " + Double.toString(cumScore / stratCount) +
        // ", " + stratCount);
        // prevName = name;
        // cumScore = 0;
        // stratCount = 0;
        // }
        // cumScore += res[i];
        // stratCount++;
        // }
        // System.out.println(prevName + ", " + Double.toString(cumScore / stratCount) +
        // ", " + stratCount);
    }
}

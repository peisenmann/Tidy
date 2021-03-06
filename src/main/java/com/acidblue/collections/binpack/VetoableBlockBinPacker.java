package com.acidblue.collections.binpack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VetoableBlockBinPacker<T extends Block<?>> extends FirstFitBinPacker<T> {

    private List<T> blocks;
    private int maximumBlockSizePerBin;
    private final List<T> badBlocks = new ArrayList<T>();

    public VetoableBlockBinPacker(final List<T> blocks,
                                  final Integer maxiumTotalSizePerBin,
                                  final Integer maximumBlocksPerBin) {

        super(maxiumTotalSizePerBin.longValue());


        if (blocks == null) {
            throw new NullPointerException("blocks cannot not be null.");
        }
        setBlocks(blocks);
        setMaximumBlockSizePerBin(maximumBlocksPerBin);

    }

    public VetoableBlockBinPacker(final long maxBinSize) {
        super(maxBinSize);
    }

    public List getPackedBlocks() {
        packBlocks();
        return getBins();
    }

    public BinPacker<T> packBlocks() {
        for (final T block : blocks) {
            if (candidateBlock(block)) {
                add(block);
            } else {
                fireIgnoredEvent(new BinEvent<T>(block, block));
            }
        }

        return this;
    }

    public Bin<T> add(final T item) {
        if (item == null) {
            throw new NullPointerException("item was null");
        }

        //todo, rewrite this. This was code from a class I lost the source for and had to decompile it.
        //this got really funky.
        Bin<T> targetBin = null;
        boolean added = false;
        int i = 0;
        int j = binList.size();

        do {
            if (i >= j) {
                break;
            }

            targetBin = binList.get(i);
            if (targetBin.getItems().size() < maximumBlockSizePerBin && (added = binList.get(i).addItem(item))) {
                break;
            }
            i++;
        } while (true);
        if (!added) {
            targetBin = createBin();
            targetBin.addItem(item);
            binList.add(targetBin);
        }
        fireAddedEvent(new BinEvent<T>(targetBin, item));
        return targetBin;
    }

    protected boolean candidateBlock(Block block) {
        return !(block instanceof BadBlock) && block.getSize() < getMaxBinSize();
    }

    public List getBadBlocks() {
        return Collections.unmodifiableList(badBlocks);
    }

    public void reset() {
        binList.clear();
        blocks.clear();
        maximumBlockSizePerBin = -1;
        maxBinSize = -1L;
    }

    public List getBlocks() {
        return blocks;
    }

    public void setBlocks(List<T> blocks) {
        this.blocks = blocks;
    }

    public Integer getMaximumBlockSizePerBin() {
        return maximumBlockSizePerBin;
    }

    public final void setMaximumBlockSizePerBin(int maxiumBlockSizePerBin) {
        if (maxiumBlockSizePerBin < 1) {
            throw new IllegalArgumentException(
                    new StringBuilder().append("maximumBlockSizePerBin must be a positive integral value (value passed was: ")
                            .append(maxiumBlockSizePerBin).append(".").toString());
        } else {
            maximumBlockSizePerBin = maxiumBlockSizePerBin;
        }
    }


}

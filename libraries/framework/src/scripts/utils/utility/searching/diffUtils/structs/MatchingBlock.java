package scripts.utils.utility.searching.diffUtils.structs;

public final class MatchingBlock {
    public int spos;
    public int dpos;
    public int length;

    @Override
    public String toString() {
        return "(" + spos + "," + dpos + "," + length + ")";
    }
}

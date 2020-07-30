package ir.sam.hearthstone.requests;

public class SelectHeroPower extends Request {
    private final int side;

    public SelectHeroPower(int side) {
        this.side = side;
    }

    @Override
    public void execute(RequestExecutor requestExecutor) {
        requestExecutor.selectHeroPower(side);
    }
}

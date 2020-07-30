package ir.sam.hearthstone.requests;

public class SelectPlayMode extends Request {
    private final String modeName;

    public SelectPlayMode(String modeName) {
        this.modeName = modeName;
    }

    @Override
    public void execute(RequestExecutor requestExecutor) {
        requestExecutor.selectPlayMode(modeName);
    }
}

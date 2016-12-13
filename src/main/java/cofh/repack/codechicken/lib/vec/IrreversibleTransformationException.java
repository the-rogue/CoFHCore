package cofh.repack.codechicken.lib.vec;

@SuppressWarnings("serial")
public class IrreversibleTransformationException extends RuntimeException {
    @SuppressWarnings("rawtypes")
	public ITransformation t;

    @SuppressWarnings("rawtypes")
	public IrreversibleTransformationException(ITransformation t) {
        this.t = t;
    }

    @Override
    public String getMessage() {
        return "The following transformation is irreversible:\n" + t;
    }
}

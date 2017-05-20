public class LocalScope extends BaseScope {
	public LocalScope() {
		super();
	}

	public LocalScope(Scope enc){
		super(enc);
	}

	public String getScopeName() {
		return "local";
	}
}
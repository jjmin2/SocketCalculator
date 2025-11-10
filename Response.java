public class Response {
	public String code;
	public double value;
	public String message;
	
	public Response() {}
	
	public Response(String code, double value, String message) {
		this.code = code;
		this.value = value;
		this.message = message;
	}
	
	public String serialize() {
		return code + ":" + value + ":" + message;
	}
	
	public static Response deserialize(String str) {
		String[] parts = str.split(":", 3);
		Response resp = new Response();
		resp.code = parts[0];
		resp.value = Double.parseDouble(parts[1]);
		resp.message = parts[2];
		return resp;
	}
}

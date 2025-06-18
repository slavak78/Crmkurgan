package maskedEditText;

public class RawText {
	private String text;
	
	public RawText() {
		text = "";
	}

	public void subtractFromString(Range range) {
		String firstPart = "";
		String lastPart = "";
		
		if(range.getStart() > 0 && range.getStart() <= text.length()) {
			firstPart = text.substring(0, range.getStart());
		}
		if(range.getEnd() >= 0 && range.getEnd() < text.length()) {
			lastPart = text.substring(range.getEnd());
		}
		text = firstPart.concat(lastPart);
	}

	public int addToString(String newString, int start, int maxLength) {
		String firstPart = "";
		String lastPart = "";
		
		if(newString == null || newString.equals("")) {
			return 0;
		}
		else if(start < 0) {
			throw new IllegalArgumentException("Start position must be non-negative");
		}
		else if(start > text.length()) {
			throw new IllegalArgumentException("Start position must be less than the actual text length");
		}
		
		int count = newString.length();
		
		if(start > 0) {
			firstPart = text.substring(0, start);
		}
		if(start < text.length()) {
			lastPart = text.substring(start);
		}
		if(text.length() + newString.length() > maxLength) {
			count = maxLength - text.length();
			newString = newString.substring(0, count);
		}
		text = firstPart.concat(newString).concat(lastPart);		
		return count;
	}

	public String getText() {
		return text;
	}

	public int length() {
		return text.length();
	}

	public char charAt(int position) {
		return text.charAt(position);
	}
}

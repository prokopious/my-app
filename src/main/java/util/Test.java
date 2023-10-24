package util;




public class Test {
	
	public static class Other {
		
		public String stringOne;
		public String stringTwo;
		
		public Other(String a, String b) {
			this.stringOne = a;
			this.stringTwo = b;
		}
		
	}

	
	
	public static void main(String[] args) {
		

		Other o = new Test.Other("one", "two");
		
		
		System.out.println(o.stringOne);
	}

}

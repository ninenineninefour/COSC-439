import java.util.Base64;
import java.util.Random;

// Encryptor used to encrypt and decrypt messages. Exchanges keys via Diffie-Hellman key exchange,
// and generates a 1 byte one-time-use pad.
public class el_Encryptor {
	private int g;
	private int n;
	private int privateKey;
	private int publicKey;
	private int sharedKey;
	private byte pad;
	private boolean ready = false;
	
	// General constructor
	public el_Encryptor(int g, int n, int privateKey) {
		this.g = g;
		this.n = n;
		this.privateKey = privateKey;
		publicKey = modularExponent(g, privateKey, n);
	}
	// Constructor for a randomly generated private key
	public el_Encryptor(int g, int n) {
		this.g = g;
		this.n = n;
		Random rand = new Random();
		// The random private key can be anything from 10 to 255.
		this.privateKey = 10 + rand.nextInt(246);
		publicKey = modularExponent(g, privateKey, n);
	}
	
	// Calculates a pad from the given public key
	public void calcPad(int otherKey) {
		sharedKey = modularExponent(otherKey, privateKey, n);
		pad = (byte)sharedKey;
		ready = true;
	}
	
	// Public key getter
	public int publicKey() {
		return publicKey;
	}
	
	// Helper method to calculate the modulo of a number raised to a power. Algorithm used is from
	// https://www.geeksforgeeks.org/modular-exponentiation-power-in-modular-arithmetic/
	private int modularExponent(int base, int pow, int mod) {
		int result = 1;
		base = base%mod;
		while(pow > 0) {
			if((pow & 1) == 0)
				result = (result*base)%mod;
			pow = pow >> 1;
			base = (base*base)%mod;
		}return result;
	}
	
	// Returns the ciphertext for the given plaintext
	public String encrypt(String plaintext) {
		if(!ready)
			return null;
		byte[] ciphertext = plaintext.getBytes();
		for(int i = 0; i < ciphertext.length; i++) {
			ciphertext[i] = (byte)(ciphertext[i] ^ pad);
		}
		// Encode in base 64 for safe transmission
		return new String(Base64.getEncoder().encode(ciphertext));
	}
	
	// Returns the plaintext for the given ciphertext
	public String decrypt(String ciphertext) {
		if(!ready)
			return null;
		// Decode from base 64
		byte[] cipherbytes = Base64.getDecoder().decode(ciphertext);
		for(int i = 0; i < cipherbytes.length; i++) {
			cipherbytes[i] = (byte)(cipherbytes[i] ^ pad);
		}
		return new String(cipherbytes);
	}
	
	// Return a string containing the relevant variables of this object
	public String toString() {
		// Pad the left side of the binary string with zeroes to make it 8 characters long
		String s = "00000000" + Integer.toBinaryString(pad);
		s = s.substring(s.length() - 8, s.length());
		return "g=" + g + ", n=" + n + ", shared key=" + sharedKey + ", pad=" + s;
	}
}

package aws_projects;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3EncryptionClient;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.S3Object;

public class AWSEncrypedDataLoader {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		
		String bucketName = "8kmiles-sivasg-training";
		String key = "demo/sample_misc.txt";
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File myFile = new File(classLoader.getResource("sample_misc.txt").getFile());
		
		KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
		keyGenerator.initialize(1024, new SecureRandom());
		KeyPair myKeyPair = keyGenerator.generateKeyPair();


		// Construct an instance of AmazonS3EncryptionClient
		//AWSCredentials credentials = new BasicAWSCredentials();
		EncryptionMaterials encryptionMaterials = new EncryptionMaterials(myKeyPair);
		AmazonS3EncryptionClient s3 = new AmazonS3EncryptionClient(new EnvironmentVariableCredentialsProvider().getCredentials(), encryptionMaterials);


		// Then just use the encryption client as normal...
		//
		// When we use the putObject method, the data in the file or InputStream
		// we specify is encrypted on the fly as it's uploaded to Amazon S3.
		s3.putObject(bucketName, key, myFile);


		// When you use the getObject method, the data retrieved from Amazon S3
		// is automatically decrypted on the fly.
		S3Object downloadedObject = s3.getObject(bucketName, key);
		
		displayTextInputStream(downloadedObject.getObjectContent());
		

	}
	
	private static void displayTextInputStream(InputStream input) throws IOException {
		    	// Read one text line at a time and display.
		        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		        while (true) {
		            String line = reader.readLine();
		            if (line == null) break;
		            System.out.println("    " + line);
		        }
		        System.out.println();
		    }

}

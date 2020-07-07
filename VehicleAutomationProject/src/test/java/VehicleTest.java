
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;  

public class VehicleTest {
	

	@Test
	public void VerifyVehicleDetails() throws Exception {

		String localDir = System.getProperty("user.dir");

		System.out.println(localDir +"\\InputData\\");

		File folder = new File(localDir +"\\InputData"); 

		String[] listOfFiles = folder.list();

		ArrayList<String> list=new ArrayList<String>();

		for (String file : listOfFiles) {

			FileReader fr = new FileReader(localDir+"//InputData//"+file); 

			BufferedReader br = new BufferedReader(fr); 

			String line;

			while((line = br.readLine()) != null)

			{ 
				line = line.trim(); 

				String[] words=line.split(" ");

				for(int i = 0; i< words.length; i++){		

					if(Pattern.matches("^[A-Z]{2}\\d{2}.*", words[i])){

						if(words[i].length() == 7)

							list.add(words[i]);

						else if(words[i].length() > 7){

							list.add(words[i].substring(0,6));

						}

						else{

							list.add(words[i]+(words[i+1].substring(0,3)));
						}
					}

				} 

			}

			fr.close();
		}

		Map<String,CarPropertiesBean>  expectedDataMap=getOutPutData();

		WebDriver driver;

		for (String curretRegistrationNum : list) {   

			String BrowserLocation = System.getProperty("user.dir");

			System.setProperty("webdriver.chrome.driver", BrowserLocation+"/Drivers/Chrome/chromedriver.exe");

			driver = new ChromeDriver();  
			
			driver.get("https://www.webuyanycar.com/");

			driver.manage().window().maximize();		

			driver.manage().timeouts().setScriptTimeout(23, TimeUnit.SECONDS);			

			driver.findElement(By.id("vehicleReg")).sendKeys(curretRegistrationNum.trim());

			driver.findElement(By.id("vehicleReg")).sendKeys(Keys.ENTER);
			
			Thread.sleep(5000);
			
			if (!driver.getPageSource().contains("Check your reg")) {

				String registration=driver.findElement(By.xpath("//*[@id=\"vehicleImage\"]/div[2]")).getText();

				String make=driver.findElement(By.xpath("//*[contains(text(),'Manufacturer:')]//following::div")).getText();

				String model=driver.findElement(By.xpath("//*[contains(text(),'Model:')]//following::div")).getText();

				String year=driver.findElement(By.xpath("//*[contains(text(),'Year:')]//following::div")).getText();

				String colour=driver.findElement(By.xpath("//*[contains(text(),'Year:')]//following::div")).getText(); 

				CarPropertiesBean bean=expectedDataMap.get(curretRegistrationNum.trim());
						
			        	if(bean.getRegistrationNum().toUpperCase().contains(registration.toUpperCase())
			        			
						&& bean.getMake().toUpperCase().contains(make.toUpperCase())
						
						&& bean.getModel().toUpperCase().contains(model.toUpperCase())
						
						&& bean.getColour().toUpperCase().contains(colour.toUpperCase())
						
						&& bean.getYear().toUpperCase().contains(year.toUpperCase())){ 
					
					System.out.println(curretRegistrationNum.trim()  +" Vehicle Details Match" );
					
				}else{
					
					System.out.println(curretRegistrationNum.trim()  +" Vehicle Details Don't Match" );
				}

			}else{
				
				System.out.println(curretRegistrationNum.trim()  +" Vehicle Is Not Found" );
			

			}
			driver.quit();
		}
	}	
	
    // Reading Output Data From File and Validating the response.
	
	public static Map<String,CarPropertiesBean> getOutPutData() throws Exception{

		String OutputfileLocation = System.getProperty("user.dir");

		Map<String,CarPropertiesBean> map=new HashMap<String,CarPropertiesBean>();

		FileReader fr = new FileReader(OutputfileLocation+"\\OutputData\\Car_Output"); 

		BufferedReader br = new BufferedReader(fr); 

		String line;

		CarPropertiesBean carPropertiesBean;

		while((line = br.readLine()) != null)

		{ 
			carPropertiesBean=new CarPropertiesBean();

			line = line.trim(); 

			String[] carPropertiesArray=line.split(",");

			for(int i = 0; i< carPropertiesArray.length; i++){	

				carPropertiesBean.setRegistrationNum(carPropertiesArray[0]);

				carPropertiesBean.setMake(carPropertiesArray[1]);

				carPropertiesBean.setModel(carPropertiesArray[2]);
				
				carPropertiesBean.setColour(carPropertiesArray[3]);

				carPropertiesBean.setYear(carPropertiesArray[4]);

			    map.put(carPropertiesArray[0], carPropertiesBean);
				

			}

		}

		return map;

	}

}


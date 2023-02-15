package Deepika.keshetty.Ass;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ClockHotelAss {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\sai kiran\\chromedriver_win32\\chromedriver.exe");
		WebDriver driver=new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://www.clock-software.com/demo-clockpms/index.html");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
		WebDriverWait w=new WebDriverWait(driver, Duration.ofSeconds(5));
		driver.findElement(By.linkText("Accommodation")).click();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String mon="Jun";
		String day="18";
		String nights="4";
		String room="Deluxe Appartment";
		String splittedPrice=null;
		int maxPack=Integer.MAX_VALUE;
		String Rate = null;
		int totalAddOnsCharge = 0;
		
		deluxApartment(driver, w);
		startBookingProcess(driver, mon, day, nights);
		splittedPrice=extractThePrice(driver, w, js, room, Rate, splittedPrice);
		maxPack=extractTheMaxPack(splittedPrice, maxPack);
		Rate=selectExpensivePack(driver, w, js, Rate, splittedPrice, maxPack);
		totalAddOnsCharge=addOnsPage(driver, w, js, nights, totalAddOnsCharge);
		validation(driver, day, mon, nights, room, Rate, totalAddOnsCharge, maxPack);
		paymentProcess(driver, w);
		confirmationPage(driver, w);
		
		driver.close();
	}
	
	public static void deluxApartment(WebDriver driver, WebDriverWait w) {
		        //selecting Apartment Delux
				w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//a[text()='Book this room'])[2]")));
				driver.findElement(By.xpath("(//a[text()='Book this room'])[2]")).click();
				driver.switchTo().frame(1);
	}
	
	public static void startBookingProcess(WebDriver driver, String mon, String day, String nights) throws InterruptedException {
		        //Selecting a valid date, and number of rooms and starting the booking process
				driver.findElement(By.id("product_search_arrival")).click();
				WebElement MDropdown=driver.findElement(By.cssSelector("[data-handler='selectMonth']"));
				Select month=new Select(MDropdown);
				month.selectByVisibleText(mon);
				List<WebElement> days=driver.findElements(By.cssSelector("[data-handler='selectDay']"));
				for(int i=0;i<days.size();i++) {
					if(days.get(i).getText().equalsIgnoreCase(day)) {
						days.get(i).click();
						break;
					}
				}
				
				driver.findElement(By.id("product_search_nights")).clear();
				driver.findElement(By.id("product_search_nights")).sendKeys(nights);
				driver.findElement(By.name("product_search[adult_count]")).sendKeys("1");
				driver.findElement(By.id("product_search_children_count")).sendKeys("0");
				Thread.sleep(1000);
				driver.findElement(By.cssSelector("input[class*='btn-lg']")).click();
	}
	
	public static String extractThePrice(WebDriver driver, WebDriverWait w, JavascriptExecutor js, String room, String Rate, String splittedPrice) {
		        //Under Deluxe Apartment, selecting the most expensive package
				driver.findElement(By.linkText("Show all")).click();
				List<WebElement> aparts=driver.findElements(By.cssSelector("div[class*='bookable-container'] div div h2"));
				for(int i=0;i<aparts.size();i++) {
					if(aparts.get(i).getText().equalsIgnoreCase(room)) {
						List<WebElement> rates=driver.findElements(By.xpath("//div[@id='bookable_container_15343']/div[2]/div[2]/table/tbody/tr/td[2]/h4"));
						for(int j=0;j<rates.size();j++) {
							String price=rates.get(j).getText();
							splittedPrice=price.split("\\.")[0].trim();
						}
					}
				}
				return splittedPrice;
	}
	
	public static int extractTheMaxPack(String splittedPrice, int maxPack) {
		int k=Integer.parseInt(splittedPrice.replace(",", ""));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(k);
		int[] arr=new int[priceList.size()];	
		for(int a=0;a<priceList.size();a++) {
			arr[a]=priceList.get(a);
			maxPack=arr[a];
			if(arr[a]>maxPack) {
				maxPack=arr[a];
			}
		}
		return maxPack;
	}
	
	public static String selectExpensivePack(WebDriver driver, WebDriverWait w, JavascriptExecutor js, String Rate, String splittedPrice, int maxPack) {
		Rate=driver.findElement(By.xpath("//div[@id='bookable_container_15343']/div[2]/div[2]/table/tbody/tr/td[2]/h4[contains(text(), '"+splittedPrice+"')]/parent::td/parent::tr/td[1]/h4")).getText().replaceAll("\n", " ");
		js.executeScript("window.scrollBy(0,500)", "");
		w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='bookable_container_15343']/div[2]/div[2]/table/tbody/tr/td[2]/following-sibling::td")));
		String max=Integer.toString(maxPack);
		while(splittedPrice.replace(",", "").equalsIgnoreCase(max)) {
			driver.findElement(By.xpath("//div[@id='bookable_container_15343']/div[2]/div[2]/table/tbody/tr/td[2]/h4[contains(text(), '"+splittedPrice+"')]/parent::td/following-sibling::td")).click();
			break;
		}
		return Rate;
	}
	
	public static int addOnsPage(WebDriver driver, WebDriverWait w, JavascriptExecutor js, String nights, int totalAddOnsCharge) {
		        //Selecting any 2 add ons
				w.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[placeholder='Quantity']")));
				String[] addOns= {"Business Services", "Dry Cleaning"};
				List<WebElement> serviceName=driver.findElements(By.xpath("//div[@class='col-sm-9 col-md-10']/div/div[1]/div[1]"));
				int a=0;
				int isplittedAddOnPrice = 0;
				String splittedAddOnPrice2;
				String quantity="1";
				int iTotal = 0;
				for(int i=0;i<serviceName.size();i++) {
					String serviceNameText=driver.findElements(By.xpath("//div[@class='col-sm-9 col-md-10']/div/div[1]/div[1]")).get(i).getText();
					String service=serviceName.get(i).getText().trim();
					List<String> addOnsList=Arrays.asList(addOns);
					if(addOnsList.contains(service)) {
						a++;
						driver.findElements(By.cssSelector("[placeholder='Quantity']")).get(i).sendKeys(quantity);
						List<WebElement> addOnsPrice=driver.findElements(By.xpath("//div[@class='col-sm-9 col-md-10']/div/div[1]/div[normalize-space(text())='"+serviceNameText+"']/parent::div/following-sibling::div/div"));
						for(int j=0;j<addOnsPrice.size();j++) {
							String addOnPrice=addOnsPrice.get(j).getText();
							String splittedAddOnPrice=addOnPrice.split("\\.")[0];
							int iQuantity=Integer.parseInt(quantity);
							
							if(!splittedAddOnPrice.contains("x")) {
								isplittedAddOnPrice=isplittedAddOnPrice+Integer.parseInt(splittedAddOnPrice)*iQuantity;
							}
							if(splittedAddOnPrice.contains("x")) {
								splittedAddOnPrice2=splittedAddOnPrice.split("\\(")[1].split("x")[1].trim();
								splittedAddOnPrice=splittedAddOnPrice2;
								int iSplittedPrice=Integer.parseInt(splittedAddOnPrice);
								int iNights=Integer.parseInt(nights);
								iTotal=iNights*iSplittedPrice*iQuantity;
							}
						}
						if(a==addOns.length) {
							break;
						}
					}
				}
				totalAddOnsCharge=isplittedAddOnPrice + iTotal;
				WebElement adds = driver.findElement(By.cssSelector("input[class='btn btn-primary btn-lg']"));
				js.executeScript("arguments[0].click();", adds);
				return totalAddOnsCharge;
	}
	
	public static void validation(WebDriver driver, String day, String mon, String nights, String room, String Rate, int totalAddOnsCharge, int maxPack) {
		        //Validating all details – Date, no of nights, room type, rate, add on (extra services charges), total
				String arrival=driver.findElement(By.xpath("//div/b[text()='Arrival']/parent::div/following-sibling::div")).getText();
				Assert.assertEquals(day+" "+mon+" 2023", arrival);
				String stay=driver.findElement(By.xpath("//div/b[text()='Stay']/parent::div/following-sibling::div")).getText();
				Assert.assertEquals(nights, stay);
				String roomType=driver.findElement(By.xpath("//div/b[text()='Room Type']/parent::div/following-sibling::div")).getText();
				Assert.assertEquals(room, roomType);
				String rate=driver.findElement(By.xpath("//div/b[text()='Rate']/parent::div/following-sibling::div")).getText();
				Assert.assertTrue(Rate.contains(rate));
				String addOnsCharge=driver.findElement(By.xpath("//div/b[text()='Extra Services']/parent::div/following-sibling::div")).getText();
				String sAddOnsCharge=Integer.toString(totalAddOnsCharge);
				Assert.assertTrue(addOnsCharge.replace(",", "").contains(sAddOnsCharge)); 
				String total=driver.findElement(By.xpath("//div[@class='row total_charges']/div/h3[normalize-space(text())='Total']/parent::div/following-sibling::div")).getText();
				int totalPrice=maxPack+totalAddOnsCharge;
				String sTotalPrice=Integer.toString(totalPrice);
				Assert.assertTrue(total.replace(",", "").contains(sTotalPrice));
	}
	
	public static void paymentProcess(WebDriver driver, WebDriverWait w) {
		        //Add traveler details and payment method to CC
				//Use a dummy Visa CC and complete payment
				driver.findElement(By.id("booking_guest_attributes_e_mail")).sendKeys("deepikak@gmail.com");
				driver.findElement(By.id("booking_guest_attributes_last_name")).sendKeys("K");
				driver.findElement(By.id("booking_guest_attributes_first_name")).sendKeys("Deepika");
				driver.findElement(By.id("booking_guest_attributes_phone_number")).sendKeys("8468387383");
				driver.findElement(By.id("booking_payment_service_credit_card_collect")).click();
				driver.findElement(By.id("booking_agreed")).click();
				driver.findElement(By.cssSelector("[value='Create Booking']")).click();
				w.until(ExpectedConditions.visibilityOfElementLocated(By.id("cardNumber")));
				driver.findElement(By.id("cardNumber")).sendKeys("4532140100626038");
				WebElement brand=driver.findElement(By.id("credit_card_collect_purchase_brand"));
				Select brandName=new Select(brand);
				brandName.selectByValue("visa");
				WebElement eDay=driver.findElement(By.id("cardExpirationMonth"));
				Select exDay=new Select(eDay);
				exDay.selectByVisibleText("10");
				WebElement eYear=driver.findElement(By.id("cardExpirationYear"));
				Select exYear=new Select(eYear);
				exYear.selectByVisibleText("2025");
				driver.findElement(By.id("credit_card_collect_purchase_address")).sendKeys("Hyderabad");
				driver.findElement(By.id("credit_card_collect_purchase_zip")).sendKeys("502032");
				driver.findElement(By.id("credit_card_collect_purchase_city")).sendKeys("Hyderabad");
				driver.findElement(By.id("credit_card_collect_purchase_state")).sendKeys("Telangana");
				WebElement countryDropdown=driver.findElement(By.id("credit_card_collect_purchase_country"));
				Select country=new Select(countryDropdown);
				country.selectByVisibleText("India");
				driver.findElement(By.cssSelector("[class='btn btn-success btn-lg btn-block']")).click();
	}
	
	public static void confirmationPage(WebDriver driver, WebDriverWait w) {
		w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='common_alert']/following-sibling::h1")));
		String confirmMessage=driver.findElement(By.xpath("//div[@id='common_alert']/following-sibling::h1")).getText();
		Assert.assertEquals("Thank you for your booking!", confirmMessage);
	}
}

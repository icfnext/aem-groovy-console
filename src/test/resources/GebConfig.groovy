import org.openqa.selenium.firefox.FirefoxDriver

driver = { new FirefoxDriver() }
baseUrl = "http://localhost:4502"
reportsDir = "target/geb-reports"
username = "${cq.username}"
password = "${cq.password}"
# selenium-lab

QA & automation lab with a focus on Selenium.

## Run tests

### Task manager application (HTML, CSS, JavaScript)

```bash
./mvnw -Dtest=TaskManagerSeleniumTest -Dheadless=false test
```

### Portfolio application (React, TypeScript)

```bash
./mvnw -Dtest=PortfolioWebsiteTest -DappUrl=http://localhost:3000 -Dheadless=false test
```

# selenium-lab

QA & automation lab with a focus on Selenium.

## Run tests

### Task manager application (HTML, CSS, JavaScript)

```bash
./mvnw -Dtest=TaskManagerSeleniumTest -Dheadless=false test
```

![01_selenium-task-manager-demo_01.gif](docs/img/01_selenium-task-manager-demo_01.gif)

### Portfolio application (React, TypeScript)

```bash
./mvnw -Dtest=PortfolioWebsiteTest -DappUrl=http://localhost:3000 -Dheadless=false test
```

![02_selenium-portfolio-website-demo_01.gif](docs/img/02_selenium-portfolio-website-demo_01.gif)

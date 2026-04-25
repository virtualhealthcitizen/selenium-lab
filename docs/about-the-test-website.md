# About the test website

The test website (included at `src/main/resources/static/index.html`) is a Task Manager app built specifically for Selenium testing.
Every interactive element has a stable, predictable ID so your tests can target them reliably.

| Element           | ID / Pattern                                    | Action                           |
|-------------------|-------------------------------------------------|----------------------------------|
| Test input        | `#task-input`                                   | Type task name                   |
| Priority dropdown | `#priority-select`                              | Select `low` / `medium` / `high` |
| Add button        | `#add-btn`                                      | Click to add task                |
| Task item         | `#task-{n}`                                     | The full row                     |
| Checkbox          | `#check-{n}`                                    | Toggle done / undone             |
| Task text label   | `#text-{n}`                                     | Read task text                   |
| Priority badge    | `#priority-{n}`                                 | Read priority                    |
| Delete button     | `#delete-{n}`                                   | Remove task                      |
| Filter buttons    | `#filter-all`, `#filter-active`, `#filter-done` | Switch views                     |
| Search box        | `#search-input`                                 | Filter by keyword                |
| Clear done        | `#clear-btn`                                    | Remove completed tasks           |
| Stats             | `#stat-total`, `stat-active`, `#stat-done`      | Assert counts                    |
| Status message    | `#status-message`                               | Read last action (aria-live)     |

### Example Selenium (Python) snippet:

```python
driver.find_element(By.ID, "task-input").send_keys("Buy groceries")
driver.find_element(By.ID, "priority-select").send_keys("high")
driver.find_element(By.ID, "add-btn").click()

# Assert it appeared
assert driver.find_element(By.ID, "stat-total").text == "4"
assert "Buy groceries" in driver.find_element(By.ID, "task-list").text

# Check the status message
status = driver.find_element(By.ID, "status-message").text
assert 'Task added' in status
```

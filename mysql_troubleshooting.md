# MySQL / MariaDB Troubleshooting Guide (XAMPP)

## Problem: MySQL shutdown unexpectedly / Port 3306 free but MySQL won’t start

### Typical Error in log:

```
Aria recovery failed. Please run aria_chk -r on all Aria tables and delete all aria_log.######## files
Plugin 'Aria' registration as a STORAGE ENGINE failed.
Failed to initialize plugins.
Aborting
```

### Cause:

- XAMPP MySQL (MariaDB) uses **Aria** storage engine.
- After Windows crash or XAMPP forced shutdown, `aria_log.*` files often become corrupted.
- MySQL fails to start.

---

## Quick Fix:

### Go to:

```
C:\xampp\mysql\data\
```

### Delete all files:

```
aria_log.*
```

(Example: `aria_log.00000001`, `aria_log.00000002`, etc.)

- Also delete:
  - `ib_logfile0`
  - `ib_logfile1`

**Do NOT delete `ibdata1`** unless you want to reset your whole DB.

### Restart MySQL in XAMPP → it should work.

---

## Prevention:

- Always **stop MySQL gracefully** before shutting down Windows or XAMPP.
- Avoid using **Force Quit** on MySQL process.

---

## Deleted `ibdata1` ?

1. Go to `http://localhost/phpmyadmin/index.php?route=/server/databases`
2. Do this steps

```sql
CREATE DATABASE ex4 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER 'ron'@'localhost' IDENTIFIED BY 'david';
GRANT ALL PRIVILEGES ON ex4.* TO 'ron'@'localhost';
FLUSH PRIVILEGES;
```

and then
edit privliges -> new user -> cancle -> user accout -> [find your user] -> edit -> Global privileges [check all] -> go

# Setup & run — TierMart (COE 528)

Pure Java, no external libraries. You just need a JDK.

---

## 1. Get a JDK (17 or newer)

```bash
java -version
javac -version
```

If either is missing, install a JDK:

- **Windows / WSL (Ubuntu):** `sudo apt install openjdk-17-jdk`
- **macOS:** `brew install openjdk@17`
- or download from adoptium.net (Temurin 17).

The project targets Java 17 (`--release 17` in the Makefile) but runs fine on 17, 21, or
newer.

---

## 2. Build & run

```bash
make build      # compiles everything into out/

make demo       # scripted State-pattern self-check (prints the tier table)
make run-cli    # interactive text menu
make run-gui    # Swing desktop window (needs a graphical display)
```

Without `make`, the same thing by hand:

```bash
mkdir -p out
javac --release 17 -d out $(find src -name '*.java')
java -cp out tiermart.app.Main demo     # or: cli  /  gui
```

> **GUI note:** `make run-gui` opens a window, so it needs a desktop. On Windows 11 + WSL
> that works through WSLg out of the box. Over plain SSH or a headless server it won't have
> a display — use `make run-cli` or `make demo` there, which need no screen.

---

## 3. Using the CLI

`make run-cli` gives you a one-key menu:

```
[p]roducts  [c]ustomers  [b]uy  [r]efund  [P]add product  [C]add customer  [q]uit
```

Try this to watch a promotion: press `b`, customer `C1`, product `P3` (the $279 monitor),
quantity `2`. The receipt shows the customer jumping from Basic straight to Gold, then `c`
shows the updated tier and discount.

---

## 4. Using the GUI

`make run-gui` opens a window with a customer dropdown, a product dropdown, a quantity
spinner, and Buy / Refund buttons. The customer table updates live — buy a few monitors for
one customer and watch the tier and discount climb; hit "Refund $100" to see it walk back.

---

## 5. Pushing it to GitHub

Create an empty `tiermart` repo on GitHub (no README), then:

```bash
cd TierMart
git init
git add .
git commit -m "COE 528: customer-tier store with the State design pattern"
git branch -M main
git remote add origin https://github.com/munevvercoskun/tiermart.git
git push -u origin main
```

The `out/` folder (compiled `.class` files) is git-ignored — source only.

---

## Troubleshooting

- **`javac: command not found`** — you have a JRE but not a JDK. Install a full JDK (step 1).
- **`make run-gui` throws `HeadlessException`** — no display available. Use the CLI or demo,
  or run it on a machine with a desktop (WSLg counts).
- **`--release 17` error on an old JDK** — your JDK is older than 17. Install 17+, or lower
  `RELEASE` in the Makefile to your version (the code uses records and switch expressions,
  so 17 is the practical floor).

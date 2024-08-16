# Life-Prognosis Management

> The Life-Prognosis Management project is a command-line utility that allows patients to input their HIV-related health information to receive an estimated lifespan based on their condition. The utility ensures the protection of user health data while providing functionality for administrators to export and aggregate data for statistical and decision-making purposes.

## Features

- **Accurate Lifespan Estimation for Patients**: Patients can receive an estimated lifespan based on their HIV-related health data.
- **Patient Management**: Patients can input, edit, and view their health details securely.
- **Data Aggregation for Admins**: Administrators can aggregate patient data for statistical analysis and decision-making.

## Built With

- **Java**: Core Java for application logic.
- **Bash**: For file manipulation and data management.

## Live Demo

[Live Demo Link](https://www.loom.com/share/3f40fddc8cbc4d77a34880e86f444300)


## Getting Started

- To get a local copy up and running follow these simple example steps.
- Clone the code to your machine, or download a ZIP of all the files directly.
- To clone, run `git clone https://github.com/UmuhireJessie/life-prognosis-management.git`
- cd into the project folder `cd life-prognosis-management`


To get a local copy up and running follow these simple example steps.

### Prerequisites

- A code editor such as Visual Studio Code, Atom, or any other of your choice.
- Java Development Kit (JDK) installed on your machine.

### Setup

**1. Clone the Repository:**
   ```bash
   git clone https://github.com/UmuhireJessie/life-prognosis-management.git
   ```

   Alternatively, you can download the project files as a ZIP and extract them.
2. **Navigate to the Project Directory:**

    `cd life-prognosis-management`

### Installation
Ensure that Java is installed on your system. You can verify this by running:

```bash
java -version
```

### Usage

**1. Setup and Initialization**
```bash
./src/scripts/user-management.sh create_store
```

This command initiates an admin with credentials set in `user-management.sh` file which are Email: admin@example.com and Password: admin123.
Note that in the future these will be set as environment variables.

**2. Compile the Project:**

```bash
javac -d bin $(find src -name "*.java")
```

This command compiles all Java files and places the compiled classes in the bin directory.

**3. Run the Project:**

```bash
java -cp bin src.Main
```

This command runs the main class of the project, launching the application.

### Run tests

### Deployment



## Authors

👤 **Marlyn Mayienga**
👤 **itsazzaosman**
👤 **Jessie Umuhire Umutesi**



- GitHub: [@Marlyn_Mayienga](https://github.com/Marlyn_Mayienga)
- GitHub: [@UMuhireJessie](https://github.com/UmuhireJessie)
- GitHub: [@itsazzaosman](https://github.com/itsazzaosman)

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

Feel free to check the [issues page](../../issues/).

## Show your support

Give a ⭐️ if you like this project!

## Acknowledgments

- Hat tip to anyone whose code was used
- Inspiration
- etc

## 📝 License

This project is [MIT](./LICENSE) licensed.

_NOTE: we recommend using the [MIT license](https://choosealicense.com/licenses/mit/) - you can set it up quickly by [using templates available on GitHub](https://docs.github.com/en/communities/setting-up-your-project-for-healthy-contributions/adding-a-license-to-a-repository). You can also use [any other license](https://choosealicense.com/licenses/) if you wish._


./src/scripts/user-management.sh update_patient_data suu@gmail.com first_name=Susan
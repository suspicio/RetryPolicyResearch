const generateRandomProfiles = () => {
    const firstNames = ["John", "Jane", "Jim", "Jessica", "Michael", "Emily"];
    const lastNames = ["Smith", "Doe", "Johnson", "Williams", "Brown", "Davis"];
    const randomFirstName = firstNames[Math.floor(Math.random() * firstNames.length)];
    const randomLastName = lastNames[Math.floor(Math.random() * lastNames.length)];
    const randomEmail = randomFirstName.toLowerCase() + "." + randomLastName.toLowerCase() + "@example.com";
    const randomDay = Math.floor(Math.random() * 29 + 1).toString().padStart(2, "0");
    const randomMonth = Math.floor(Math.random() * 12 + 1).toString().padStart(2, "0");
    const randomYear = Math.floor(Math.random() * 50 + 1950).toString();
    const randomBirthday = `${randomDay}/${randomMonth}/${randomYear}`;

    return {
        name: randomFirstName,
        surname: randomLastName,
        email: randomEmail,
        birthday: randomBirthday
    };
}

export { generateRandomProfiles }
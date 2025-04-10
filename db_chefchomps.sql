CREATE DATABASE RecetasDB;
USE RecetasDB;

CREATE TABLE recetas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    image VARCHAR(500) NOT NULL,
    imageType VARCHAR(50) NOT NULL,
    servings INT NOT NULL,
    readyInMinutes INT NOT NULL,
    cookingMinutes INT NULL,
    preparationMinutes INT NULL,
    license VARCHAR(255) NULL,
    sourceName VARCHAR(255) NOT NULL,
    sourceUrl VARCHAR(500) NOT NULL,
    spoonacularSourceUrl VARCHAR(500) NOT NULL,
    healthScore DOUBLE NOT NULL,
    spoonacularScore DOUBLE NOT NULL,
    pricePerServing DOUBLE NOT NULL,
    analyzedInstructions TEXT NOT NULL,
    cheap BOOLEAN NOT NULL,
    creditsText VARCHAR(255) NOT NULL,
    cuisines TEXT NOT NULL,
    dairyFree BOOLEAN NOT NULL,
    diets TEXT NOT NULL,
    gaps VARCHAR(100) NOT NULL,
    glutenFree BOOLEAN NOT NULL,
    instructions TEXT NOT NULL,
    ketogenic BOOLEAN NOT NULL,
    lowFodmap BOOLEAN NOT NULL,
    occasions TEXT NOT NULL,
    sustainable BOOLEAN NOT NULL,
    vegan BOOLEAN NOT NULL,
    vegetarian BOOLEAN NOT NULL,
    veryHealthy BOOLEAN NOT NULL,
    veryPopular BOOLEAN NOT NULL,
    whole30 BOOLEAN NOT NULL,
    weightWatcherSmartPoints INT NOT NULL,
    dishTypes TEXT NOT NULL,
    extendedIngredients TEXT NOT NULL,
    summary TEXT NOT NULL,
    winePairing TEXT NOT NULL
);

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    id_receta INT,
    FOREIGN KEY (id_receta) REFERENCES recetas(id) ON DELETE SET NULL
);

INSERT INTO usuarios values (1, "correo@ejemplo.com", "123465abc", "Juan", "Perez", 1);
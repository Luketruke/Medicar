const express = require("express");
const mysql = require("mysql2");
require("dotenv").config();

const app = express();
app.use(express.json());

const pool = mysql.createPool({
    host: "sql10.freesqldatabase.com",
    port: "3306",
    user: "sql10749648",
    password: "E5Xug5vSJj",
    database: "sql10749648",
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0,
});

app.get("/verificar-email", (req, res) => {
    const { token } = req.query;

    if (!token) {
        return res.status(400).json({ error: "Token no proporcionado." });
    }

    const query =
        "UPDATE usuarios SET email_verificado = 1, token = null WHERE token = ?";

    pool.query(query, [token], (err, result) => {
        if (err) {
            console.error("Error al actualizar la base de datos:", err);
            return res
                .status(500)
                .json({ error: "Error al actualizar la base de datos." });
        }

        if (result.affectedRows === 0) {
            return res
                .status(404)
                .json({ error: "Token no valido o no encontrado." });
        }

        res.json({ message: "Email verificado exitosamente." });
    });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Servidor corriendo en el puerto ${PORT}`);
});

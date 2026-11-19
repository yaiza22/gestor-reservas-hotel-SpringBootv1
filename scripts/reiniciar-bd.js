// Borra las dos bases del proyecto. Primero hay que detener antes los servicios y se arrancan después para recargar la demostración.
['hotel_gestion', 'hotel_habitaciones'].forEach((nombre) => {
    const resultado = db.getSiblingDB(nombre).dropDatabase();
    print(nombre + ': ' + JSON.stringify(resultado));
});
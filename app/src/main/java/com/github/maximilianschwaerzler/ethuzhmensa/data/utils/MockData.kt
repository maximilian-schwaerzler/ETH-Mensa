package com.github.maximilianschwaerzler.ethuzhmensa.data.utils

import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Offer
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.MenuWithPrices
import java.time.LocalDate

object MockData {
    val offers = listOf(
        OfferWithPrices(
            offer = Offer(
                id = 1219,
                facilityId = 9,
                date = LocalDate.of(2025, 2, 17),
            ),
            menus = listOf(
                MenuWithPrices(
                    menu = Offer.Menu(
                        id = 29728,
                        offerId = 1219,
                        name = "STREET",
                        mealName = "ARANCINI BOLOGNESE",
                        mealDescription = "Mozzarella | Ratatouille | Reibkäse | Salat",
                        imageUrl = "https://idapps.ethz.ch/cookpit-pub-services/v1/images/38670"
                    ),
                    prices = listOf(
                        Offer.Menu.MenuPrice(
                            id = 1,
                            menuId = 29728,
                            price = Price(9.5),
                            customerGroupId = 10,
                            customerGroupDesc = "Studierende",
                            customerGroupDescShort = "Stud."
                        ),
                        Offer.Menu.MenuPrice(
                            id = 2,
                            menuId = 29728,
                            price = Price(11.5),
                            customerGroupId = 11,
                            customerGroupDesc = "Interne",
                            customerGroupDescShort = "Int."
                        ),
                        Offer.Menu.MenuPrice(
                            id = 3,
                            menuId = 29728,
                            price = Price(15.5),
                            customerGroupId = 13,
                            customerGroupDesc = "Extern",
                            customerGroupDescShort = "Ext."
                        )
                    )
                ),
                MenuWithPrices(
                    menu = Offer.Menu(
                        id = 29729,
                        offerId = 1219,
                        name = "GARDEN",
                        mealName = "GRATIN FLORENTINER ART",
                        mealDescription = "Kartoffeln | Bechamel | Spinat | Reibkäse | Spiegelei | Tomatenpesto | Frucht"
                    ),
                    prices = listOf(
                        Offer.Menu.MenuPrice(
                            id = 4,
                            menuId = 29729,
                            price = Price(7.0),
                            customerGroupId = 10,
                            customerGroupDesc = "Studierende",
                            customerGroupDescShort = "Stud."
                        ),
                        Offer.Menu.MenuPrice(
                            id = 5,
                            menuId = 29729,
                            price = Price(11.5),
                            customerGroupId = 11,
                            customerGroupDesc = "Interne",
                            customerGroupDescShort = "Int."
                        ),
                        Offer.Menu.MenuPrice(
                            id = 6,
                            menuId = 29729,
                            price = Price(14.5),
                            customerGroupId = 13,
                            customerGroupDesc = "Extern",
                            customerGroupDescShort = "Ext."
                        )
                    )
                )
            )
        ),
        OfferWithPrices(
            offer = Offer(
                id = 1220,
                facilityId = 9,
                date = LocalDate.of(2025, 2, 18),
            ),
            menus = listOf(
                MenuWithPrices(
                    menu = Offer.Menu(
                        id = 37222,
                        offerId = 1220,
                        name = "STREET",
                        mealName = "Ausverkauft!",
                        mealDescription = "-"
                    ),
                    prices = listOf(
                        Offer.Menu.MenuPrice(
                            id = 10,
                            menuId = 37222,
                            price = Price(11.5),
                            customerGroupId = 10,
                            customerGroupDesc = "Studierende",
                            customerGroupDescShort = "Stud."
                        ),
                        Offer.Menu.MenuPrice(
                            id = 11,
                            menuId = 37222,
                            price = Price(13.5),
                            customerGroupId = 11,
                            customerGroupDesc = "Interne",
                            customerGroupDescShort = "Int."
                        ),
                        Offer.Menu.MenuPrice(
                            id = 12,
                            menuId = 37222,
                            price = Price(17.5),
                            customerGroupId = 13,
                            customerGroupDesc = "Extern",
                            customerGroupDescShort = "Ext."
                        )
                    )
                ),
                MenuWithPrices(
                    menu = Offer.Menu(
                        id = 37223,
                        offerId = 1220,
                        name = "GARDEN",
                        mealName = "PAD THAI",
                        mealDescription = "Tofu | Ei | Weizennudeln | Frühlingszwiebeln | Zucchetti | Erdnüsse | Tamarinden | Sojasauce | Limette"
                    ),
                    prices = listOf(
                        Offer.Menu.MenuPrice(
                            id = 13,
                            menuId = 37223,
                            price = Price(7.0),
                            customerGroupId = 10,
                            customerGroupDesc = "Studierende",
                            customerGroupDescShort = "Stud."
                        ),
                        Offer.Menu.MenuPrice(
                            id = 14,
                            menuId = 37223,
                            price = Price(11.5),
                            customerGroupId = 11,
                            customerGroupDesc = "Interne",
                            customerGroupDescShort = "Int."
                        ),
                        Offer.Menu.MenuPrice(
                            id = 15,
                            menuId = 37223,
                            price = Price(14.5),
                            customerGroupId = 13,
                            customerGroupDesc = "Extern",
                            customerGroupDescShort = "Ext."
                        )
                    )
                )
            )
        )
    )

    val facilities = listOf(
        Facility(
            id = 1,
            name = "bQm",
            location = "MM C"
        ),
        Facility(
            id = 2,
            name = "CafeBar",
            location = "HG E"
        ),
        Facility(
            id = 3,
            name = "Clausiusbar",
            location = "CLA D"
        ),
        Facility(
            id = 4,
            name = "Kiosk CLA",
            location = "CLA D"
        ),
        Facility(
            id = 5,
            name = "Dozentenfoyer",
            location = "HG K30.5"
        ),
        Facility(
            id = 6,
            name = "Einstein & Zweistein",
            location = "MM C82"
        ),
        Facility(
            id = 7,
            name = "food&lab",
            location = "CAB H41"
        ),
        Facility(
            id = 8,
            name = "Archimedes",
            location = "GLC E"
        ),
        Facility(
            id = 9,
            name = "Mensa Polyterrasse",
            location = "MM B79"
        ),
        Facility(
            id = 10,
            name = "Polysnack",
            location = "HG F32"
        ),
        Facility(
            id = 11,
            name = "Tannenbar",
            location = "ML E23"
        ),
        Facility(
            id = 12,
            name = "Eureka Take Away",
            location = "GLC E"
        ),
        Facility(
            id = 13,
            name = "Zwei Grad Bistro",
            location = "CHN D9"
        ),
        Facility(
            id = 14,
            name = "Alumni quattro Lounge",
            location = "HIL D57.2"
        ),
    )

    val facilitiesWithOffers =
        facilities.map { facility ->
            facility to offers.find { it.offer.facilityId == facility.id }
        }
}
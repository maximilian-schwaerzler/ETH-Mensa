# ETH Zurich Mensa API

## Endpoints
### Menu Endpoint
Base URL: https://idapps.ethz.ch/cookpit-pub-services/v1/weeklyrotas

This endpoint is used to get the menu for a specific mensa for a certain week

**Query Parameters**

| Name           | Value        | Description                                                                             |
|----------------|--------------|-----------------------------------------------------------------------------------------|
| `client-id`    | `ethz-wcms`  | The client, always `ethz-wcms`                                                          |
| `lang`         | `de`         | The result language                                                                     |
| `rs-first`     | `0`          | ?                                                                                       |
| `rs-size`      | `50`         | ?                                                                                       |
| `valid-after`  | `YYYY-MM-DD` | The date of the first day for the menu<br/>**Included**                                 |
| `valid-before` | `YYYY-MM-DD` | The date of the first day for the menu<br/>**Excluded**                                 |
| `facility`     | `INT`        | ID of the mensa you want to get the menu for<br/>An integer between 1 and 23 (included) |

### Description Endpoint
Base URL: https://idapps.ethz.ch/cookpit-pub-services/v1/facilities/<mensa id>


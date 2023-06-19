# powerwatchd

Daemon to palliate lack of USB monitoring for my UPS.
It works the following way :

- watch internet (via ping 1.1.1.1 / 8.8.8.8)
- If there's no connection for more than 5 minutes, shutdown main services


Everything is licensed under EPL 2 (in LICENSE), except if mentionned otherwise.

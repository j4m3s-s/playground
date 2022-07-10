#!/bin/sh

set -ex

python manage.py makemigrations
python manage.py migrate
# FIXME: maybe needed in the future
#python manage.py collectstatic --noinput

# FIXME: wsgi or something ?
exec python manage.py runserver 0.0.0.0

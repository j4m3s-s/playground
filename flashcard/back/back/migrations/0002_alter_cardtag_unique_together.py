# Generated by Django 4.0.5 on 2022-07-03 21:21

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('back', '0001_initial'),
    ]

    operations = [
        migrations.AlterUniqueTogether(
            name='cardtag',
            unique_together={('card', 'tag')},
        ),
    ]

# Generated by Django 4.0.5 on 2022-07-17 14:13

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('back', '0009_alter_cardtag_card'),
    ]

    operations = [
        migrations.AlterField(
            model_name='cardtag',
            name='card',
            field=models.ForeignKey(on_delete=django.db.models.deletion.DO_NOTHING, to='back.card'),
        ),
        migrations.AlterField(
            model_name='testworkflowquestion',
            name='card',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='back.card'),
        ),
    ]

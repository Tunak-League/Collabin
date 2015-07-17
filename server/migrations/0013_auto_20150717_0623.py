# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('server', '0012_auto_20150716_0332'),
    ]

    operations = [
        migrations.AlterField(
            model_name='skills',
            name='projects',
            field=models.ManyToManyField(related_name='skills', to='server.Projects'),
        ),
        migrations.AlterField(
            model_name='skills',
            name='users',
            field=models.ManyToManyField(related_name='skills', to='server.UserProfiles'),
        ),
        migrations.AlterField(
            model_name='types',
            name='users',
            field=models.ManyToManyField(related_name='types', to='server.UserProfiles'),
        ),
    ]

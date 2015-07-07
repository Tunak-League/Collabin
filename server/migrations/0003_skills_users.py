# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('server', '0002_skills_types'),
    ]

    operations = [
        migrations.AddField(
            model_name='skills',
            name='users',
            field=models.ManyToManyField(to='server.Users'),
        ),
    ]

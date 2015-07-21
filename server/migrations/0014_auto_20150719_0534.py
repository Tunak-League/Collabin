# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('server', '0013_auto_20150717_0623'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='projects',
            name='id_types',
        ),
        migrations.AddField(
            model_name='types',
            name='projects',
            field=models.ManyToManyField(related_name='types', to='server.Projects'),
        ),
    ]

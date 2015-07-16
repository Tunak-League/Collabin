# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('server', '0010_auto_20150716_0256'),
    ]

    operations = [
        migrations.AlterField(
            model_name='userprofiles',
            name='image_path',
            field=models.CharField(default=None, max_length=51, null=True, blank=True),
        ),
    ]

# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('server', '0002_auto_20160124_1938'),
    ]

    operations = [
        migrations.AlterField(
            model_name='userprofiles',
            name='user_image',
            field=models.TextField(default=None, null=True, blank=True),
        ),
    ]

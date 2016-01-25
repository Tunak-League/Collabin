# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('server', '0003_auto_20160124_2227'),
    ]

    operations = [
        migrations.AlterField(
            model_name='projects',
            name='project_image',
            field=models.TextField(default=None, null=True, blank=True),
        ),
    ]

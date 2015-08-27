# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('server', '0002_auto_20150822_0154'),
    ]

    operations = [
        migrations.AlterField(
            model_name='userprofiles',
            name='device',
            field=models.ForeignKey(default=None, to='push_notifications.GCMDevice'),
        ),
    ]
